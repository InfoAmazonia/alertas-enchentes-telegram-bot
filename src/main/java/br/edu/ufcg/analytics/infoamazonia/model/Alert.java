package br.edu.ufcg.analytics.infoamazonia.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.edu.ufcg.analytics.infoamazonia.River;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    public Long userId;
    public River river;

    protected Alert() {}
    
    public Alert(Long userId, River river) {
		this.userId = userId;
		this.river = river;
	}

    @Override
	public String toString() {
		return "Alert [userId=" + userId + ", river=" + river + "]";
	}

}