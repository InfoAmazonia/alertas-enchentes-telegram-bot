package br.edu.ufcg.analytics.infoamazonia.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.edu.ufcg.analytics.infoamazonia.State;

@Entity
public class Conversation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    public Integer userId;

    public Long chatId;
    
    public State state;
    
    protected Conversation() {}

	public Conversation(Integer userId, Long chatId, State state) {
		super();
		this.userId = userId;
		this.chatId = chatId;
		this.state = state;
	}

	@Override
	public String toString() {
		return "Conversation [userId=" + userId + ", chatId=" + chatId + ", state=" + state + "]";
	}
}