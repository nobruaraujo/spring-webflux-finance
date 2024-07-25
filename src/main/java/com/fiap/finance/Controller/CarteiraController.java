package com.fiap.finance.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.finance.Model.Carteira;
import com.fiap.finance.Request.CarteiraComAcoesRequest;
import com.fiap.finance.Service.CarteiraService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;


@RestController
@RequestMapping("/carteiras")
public class CarteiraController {

    @Autowired
    private CarteiraService carteiraService;

    @GetMapping
    public Flux<Carteira> getAll() {
        return carteiraService.findAll();
    }

    @PostMapping
    public Mono<Carteira> save(@RequestBody CarteiraComAcoesRequest carteira) {
        return carteiraService.save(carteira.nome, carteira.acoes);
    }

    @GetMapping("/{carteiraNome}/rentabilidade")
    public Mono<ResponseEntity<Double>> calcularRentabilidade(@PathVariable String carteiraNome) {
        return carteiraService.calcularRentabilidade(carteiraNome)
                .map(totalAmount -> ResponseEntity.ok(totalAmount))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tuple2<Long, Carteira>> getCarteiraEvents(){
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(10));
        Flux<Carteira> events = carteiraService.findAll();
        return Flux.zip(interval, events);
    }
   
}
