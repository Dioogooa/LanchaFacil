package com.lanchonete.common.enums;

import com.lanchonete.common.enums.PedidoStatus;
import com.lanchonete.event.PedidoCriadoEvent;
import com.lanchonete.rabbit.PedidoProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestProducerRunner implements CommandLineRunner {

    private final PedidoProducer pedidoProducer;

    @Override
    public void run(String... args) {

        PedidoCriadoEvent event = new PedidoCriadoEvent();

        event.setPedidoId(1L);
        event.setClienteNome("João");
        event.setClienteEmail("joao@gmail.com");
        event.setStatus(PedidoStatus.AGUARDANDO);

        pedidoProducer.enviarPedidoCriado(event);
    }

}