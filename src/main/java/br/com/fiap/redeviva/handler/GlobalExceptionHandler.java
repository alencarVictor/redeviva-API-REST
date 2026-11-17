package br.com.fiap.redeviva.handler;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.exception.RegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice //captura as exceptions de todos os controllers e devolve o JSON padronizado
public class GlobalExceptionHandler {

    //404: o BO nao achou o registro
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> recursoNaoEncontrado(RecursoNaoEncontradoException e) {
        return erro(HttpStatus.NOT_FOUND, e.getMessage());
        //status not found 404
    }

    //404: rota que nao existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> rotaNaoEncontrada(NoResourceFoundException e) {
        return erro(HttpStatus.NOT_FOUND, "Rota não encontrada");
        //status not found 404
    }

    //400: campos invalidos nos DTOs (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError campo : e.getBindingResult().getFieldErrors()) {
            campos.put(campo.getField(), campo.getDefaultMessage());
        }
        Map<String, Object> corpo = montarCorpo(HttpStatus.BAD_REQUEST, "Dados inválidos");
        corpo.put("campos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
        //status bad request 400
    }

    //400: JSON mal formatado ou com tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e) {
        return erro(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado");
        //status bad request 400
    }

    //400: valor errado na rota (ex: /lojas/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return erro(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro " + e.getName());
        //status bad request 400
    }

    //405: metodo HTTP que a rota nao aceita
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> metodoNaoSuportado(HttpRequestMethodNotSupportedException e) {
        return erro(HttpStatus.METHOD_NOT_ALLOWED, "Método " + e.getMethod() + " não suportado para esta rota");
        //status method not allowed 405
    }

    //409: regra de negocio violada
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> regraDeNegocio(RegraDeNegocioException e) {
        return erro(HttpStatus.CONFLICT, e.getMessage());
        //status conflict 409
    }

    //erros do Oracle: o detalhe do SQL vai so para o console, o cliente recebe uma mensagem limpa
    @ExceptionHandler(BancoDeDadosException.class)
    public ResponseEntity<Map<String, Object>> bancoDeDados(BancoDeDadosException e) {
        System.out.println("Erro de banco (ORA-" + e.getCodigo() + "): " + e.getMessage());
        if (e.getCodigo() == 1) {
            return erro(HttpStatus.CONFLICT, "Já existe um registro com esses dados"); //unique
        } else if (e.getCodigo() == 2292) {
            return erro(HttpStatus.CONFLICT, "Não é possível remover: existem registros que dependem deste"); //filhos
        } else if (e.getCodigo() == 2291) {
            return erro(HttpStatus.BAD_REQUEST, "Registro relacionado não encontrado"); //chave estrangeira
        } else if (e.getCodigo() == 2290 || e.getCodigo() == 1400 || e.getCodigo() == 1438) {
            return erro(HttpStatus.BAD_REQUEST, "Dados inválidos para o banco de dados"); //check, not null ou valor grande demais
        } else {
            return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao acessar o banco de dados");
        }
    }

    //500: qualquer erro inesperado (mensagem generica, sem stack trace)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> erroInesperado(Exception e) {
        System.out.println("Erro inesperado: " + e.getMessage());
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        //status internal server error 500
    }

    //monta o JSON padrao de erro
    private Map<String, Object> montarCorpo(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", status.value());
        corpo.put("mensagem", mensagem);
        return corpo;
    }

    private ResponseEntity<Map<String, Object>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(montarCorpo(status, mensagem));
    }
}