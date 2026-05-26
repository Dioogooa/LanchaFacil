package com.lanchonete.email;

import com.lanchonete.notificacao.NotificacaoEvento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${app.mail.provider}")
    private String provider;

    public void enviarAtualizacaoPedido(NotificacaoEvento evento) {
        if (!StringUtils.hasText(evento.clienteEmail())) {
            throw new IllegalArgumentException(
                    "Email do cliente ausente para o pedido " + evento.pedidoId()
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(evento.clienteEmail());
        message.setSubject("Atualizacao do pedido #" + evento.pedidoId());
        message.setText("""
                Ola %s,

                O seu pedido de %s esta com o status: %s.

                Mensagem: %s
                Provedor configurado: %s
                """.formatted(
                evento.clienteNome(),
                evento.item(),
                evento.status(),
                evento.mensagem(),
                provider
        ));

        try {
            mailSender.send(message);
            log.info(
                    "Email enviado para {} (pedido {}, via {})",
                    evento.clienteEmail(),
                    evento.pedidoId(),
                    provider
            );
        } catch (Exception exception) {
            log.error(
                    "Falha ao enviar email do pedido {} para {}: {}",
                    evento.pedidoId(),
                    evento.clienteEmail(),
                    exception.getMessage(),
                    exception
            );
            throw exception;
        }
    }
}
