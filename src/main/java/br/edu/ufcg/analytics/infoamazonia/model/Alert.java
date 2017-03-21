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
    
    public Long chatId;
    public River river;

    protected Alert() {}
    
    public Alert(Long chatId, River river) {
		this.chatId = chatId;
		this.river = river;
	}

    @Override
	public String toString() {
		return "Alert [chatId=" + chatId + ", river=" + river + "]";
	}

}