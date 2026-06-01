package br.com.runly.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "corridas")
public class Corrida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double distanciaKm;

    @Column(nullable = false)
    private Integer tempoSegundos;

    @Column(nullable = false)
    private Double paceMedio;

    @Column(nullable = false)
    private LocalDateTime dataCorrida;

    @Column(columnDefinition = "TEXT")
    private String rotaJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public Corrida() {
    }

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        if (this.dataCorrida == null) {
            this.dataCorrida = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Integer getTempoSegundos() {
        return tempoSegundos;
    }

    public void setTempoSegundos(Integer tempoSegundos) {
        this.tempoSegundos = tempoSegundos;
    }

    public Double getPaceMedio() {
        return paceMedio;
    }

    public void setPaceMedio(Double paceMedio) {
        this.paceMedio = paceMedio;
    }

    public LocalDateTime getDataCorrida() {
        return dataCorrida;
    }

    public void setDataCorrida(LocalDateTime dataCorrida) {
        this.dataCorrida = dataCorrida;
    }

    public String getRotaJson() {
        return rotaJson;
    }

    public void setRotaJson(String rotaJson) {
        this.rotaJson = rotaJson;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
