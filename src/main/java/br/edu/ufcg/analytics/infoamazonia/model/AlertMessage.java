package br.edu.ufcg.analytics.infoamazonia.model;

public class AlertMessage {

    public Long riverId;
    public Long timestamp;
    public String message;

    protected AlertMessage() {}

	public AlertMessage(Long riverId, Long timestamp, String message) {
		super();
		this.riverId = riverId;
		this.timestamp = timestamp;
		this.message = message;
	}

	@Override
	public String toString() {
		return "AlertMessage [riverId=" + riverId + ", timestamp=" + timestamp + ", message=" + message + "]";
	}
}