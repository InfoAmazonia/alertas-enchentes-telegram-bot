package br.edu.ufcg.analytics.infoamazonia.database;

import org.springframework.data.repository.CrudRepository;

import br.edu.ufcg.analytics.infoamazonia.model.Conversation;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {

    Conversation findFirstByUserIdAndChatId(Integer userId, Long chatId);
    
}