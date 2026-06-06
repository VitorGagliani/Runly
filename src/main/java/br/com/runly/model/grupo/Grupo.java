package br.com.runly.model.grupo;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;



import br.com.runly.model.Usuario;

@Entity
@Table(name = "grupos")
public class Grupo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	
	@Column(nullable = false, length = 50)
	private String nome;
	
    @ManyToMany
    @JoinTable(
        name = "grupo_membros",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> membros = new ArrayList<>();
	
	
	@Column(length = 300)
	private String descricao;
	
	@ManyToOne
	@JoinColumn(name = "fundador_id", nullable = false)
	private Usuario fundador;
	
	@ManyToMany
	@JoinTable(
	    name = "grupo_administradores",
	    joinColumns = @JoinColumn(name = "grupo_id"),
	    inverseJoinColumns = @JoinColumn(name = "usuario_id")
	)
	private List<Usuario> administradores = new ArrayList<>();
	
	
	private String fotoPerfil;
	

	public String getFotoPerfil() {
	    return fotoPerfil;
	}

	public void setFotoPerfil(String fotoPerfil) {
	    this.fotoPerfil = fotoPerfil;
	}
	
	public Usuario getFundador() {
		return fundador;
	}


	public void setFundador(Usuario fundador) {
		this.fundador = fundador;
	}


	public List<Usuario> getAdministradores() {
		return administradores;
	}


	public void setAdministradores(List<Usuario> administradores) {
		this.administradores = administradores;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public List<Usuario> getMembros() {
		return membros;
	}


	public void setMembros(List<Usuario> membros) {
		this.membros = membros;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	
	

}
