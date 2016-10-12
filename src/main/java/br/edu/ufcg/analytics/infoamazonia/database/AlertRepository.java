package br.edu.ufcg.analytics.infoamazonia.database;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.edu.ufcg.analytics.infoamazonia.River;
import br.edu.ufcg.analytics.infoamazonia.model.Alert;

public interface AlertRepository extends CrudRepository<Alert, Long> {

    Alert findFirstByUserIdAndRiver(Integer userId, River river);

    List<Alert> findAllByRiver(River river);

    List<Alert> findAllByUserId(Integer userId);
}