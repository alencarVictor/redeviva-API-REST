package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.ClienteDAO;
import br.com.fiap.redeviva.dao.LojaDAO;
import br.com.fiap.redeviva.dao.ReservaDAO;
import br.com.fiap.redeviva.dto.ReservaDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.exception.RegraDeNegocioException;
import br.com.fiap.redeviva.model.Cliente;
import br.com.fiap.redeviva.model.Reserva;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

@Service
public class ReservaBO {

    private final ReservaDAO reservaDAO;
    private final ClienteDAO clienteDAO;
    private final LojaDAO lojaDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public ReservaBO(ReservaDAO reservaDAO, ClienteDAO clienteDAO, LojaDAO lojaDAO) {
        this.reservaDAO = reservaDAO;
        this.clienteDAO = clienteDAO;
        this.lojaDAO = lojaDAO;
    }

    public List<ReservaDTO> findAll() {
        List<ReservaDTO> reservas = new ArrayList<>();
        for (Reserva reserva : reservaDAO.findAll()) {
            reservas.add(modelMapper.map(reserva, ReservaDTO.class)); //model -> dto
        }
        return reservas;
    }

    public List<ReservaDTO> findByCliente(Long cdCliente) {
        if (clienteDAO.findByCodigo(cdCliente) == null) {
            throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
        }
        List<ReservaDTO> reservas = new ArrayList<>();
        for (Reserva reserva : reservaDAO.findByCliente(cdCliente)) {
            reservas.add(modelMapper.map(reserva, ReservaDTO.class));
        }
        return reservas;
    }

    public ReservaDTO findByCodigo(Long cdReserva) {
        Reserva reserva = reservaDAO.findByCodigo(cdReserva);
        if (reserva == null) {
            throw new RecursoNaoEncontradoException("Reserva " + cdReserva + " não encontrada");
        }
        return modelMapper.map(reserva, ReservaDTO.class);
    }

    public ReservaDTO save(Long cdCliente, ReservaDTO dto) {
        Cliente cliente = clienteDAO.findByCodigo(cdCliente);
        if (cliente == null) {
            throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
        }
        if (lojaDAO.findByCodigo(dto.getCdLoja()) == null) {
            throw new RecursoNaoEncontradoException("Loja " + dto.getCdLoja() + " não encontrada");
        }
        //regra: so cliente com cadastro validado pode reservar
        if (!cliente.getStValidacao().equals("VALIDADO")) {
            throw new RegraDeNegocioException("Cliente com cadastro não validado não pode reservar");
        }
        //regra: o cadastro validado tem prazo de validade
        if (cliente.getDtValidadeCadastro().isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("O cadastro do cliente está vencido");
        }
        //regra: no maximo 2 reservas por semana (janela dos ultimos 7 dias)
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        if (reservaDAO.contarDesde(cdCliente, agora.minusDays(7)) >= 2) {
            throw new RegraDeNegocioException("Limite de 2 reservas por semana atingido");
        }
        Reserva reserva = modelMapper.map(dto, Reserva.class); //dto -> model
        reserva.setCdReserva(null); //o id vem da sequence, ignora o que chegar no JSON
        reserva.setCdCliente(cdCliente); //o cliente vem da rota
        reserva.setCdRetirada(gerarCodigoRetirada());
        reserva.setDtReserva(agora);
        reserva.setDtExpiracao(agora.plusDays(1)); //regra das 24h, calculada da mesma data
        reserva.setStReserva("ATIVA"); //toda reserva nova comeca ativa
        return modelMapper.map(reservaDAO.save(reserva), ReservaDTO.class);
    }

    public ReservaDTO update(Long cdReserva, ReservaDTO dto) {
        Reserva reserva = reservaDAO.findByCodigo(cdReserva);
        if (reserva == null) {
            throw new RecursoNaoEncontradoException("Reserva " + cdReserva + " não encontrada");
        }
        //regra: so da para alterar uma reserva que ainda esta ativa
        if (!reserva.getStReserva().equals("ATIVA")) {
            throw new RegraDeNegocioException("Só é possível alterar uma reserva ativa");
        }
        reserva.setDsFormaPagamento(dto.getDsFormaPagamento()); //so a forma de pagamento muda
        reservaDAO.update(reserva);
        return modelMapper.map(reserva, ReservaDTO.class);
    }

    public void delete(Long cdReserva) {
        if (!reservaDAO.delete(cdReserva)) {
            throw new RecursoNaoEncontradoException("Reserva " + cdReserva + " não encontrada");
        }
    }

    //codigo de retirada com 8 caracteres (sem 0, O, 1 e I para nao confundir no balcao)
    private String gerarCodigoRetirada() {
        String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();
        String codigo = "";
        for (int i = 0; i < 8; i++) {
            codigo = codigo + caracteres.charAt(random.nextInt(caracteres.length()));
        }
        return codigo;
    }
}