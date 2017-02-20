package br.edu.ufcg.analytics.infoamazonia.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.edu.ufcg.analytics.infoamazonia.River;

@Entity
public class AlertDemo {

    @Id
    public Integer userId;
    
    public River river;
    public Long timestamp;

    protected AlertDemo() {}
    
    public AlertDemo(Integer userId, River river, Long timestamp) {
		this.userId = userId;
		this.river = river;
		this.timestamp = timestamp;
	}

    @Override
	public String toString() {
		return "AlertDemo [userId=" + userId + ", river=" + river + ", timestamp=" + timestamp + "]";
	}

}