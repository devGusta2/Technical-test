package com.gustavorodrigues.user_management_api.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException excep) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Usuário não encontrado!");
        problem.setDetail(excep.getMessage());
        return problem;
    }

    @ExceptionHandler(EmailAlreadExistsEception.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadExistsEception excep) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("E-mail já cadastrado no sistema");
        problem.setDetail(excep.getMessage());
        return problem;
    }

    @ExceptionHandler(InvalidCepException.class)
    public ProblemDetail handleInvalidCep(InvalidCepException excep) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Cep inválido");
        problem.setDetail(excep.getMessage());
        return problem;
    }

    @ExceptionHandler(CredentialsException.class)
    public ProblemDetail handleCredential(CredentialsException excep) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Credenciais inválidas");
        problem.setDetail(excep.getMessage());
        return problem;
    }

    @ExceptionHandler(BussinesException.class)
    public ProblemDetail handleBusinessException(BussinesException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problem.setTitle("Violação da regra de negócio");
        problem.setDetail(exception.getMessage());

        return problem;
    }

}
