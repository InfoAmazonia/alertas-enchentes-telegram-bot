package br.edu.ufcg.analytics.infoamazonia;

public enum River {
	RIO_BRANCO(13600010L, "Rio Acre"), RIO_MADEIRA(15400000L, "Rio Madeira");

	private final Long code;
	private final String name;

	private River(Long code, String name) {
		this.code = code;
		this.name = name;
	}

	public Long getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}

	public static River fromName(String name) {
		for (River rio : values()) {
			if(name.equals(rio.getName())){
				return rio;
			}
		}
		return null;
	}
	
	public static River fromCode(Long code) {
		for (River rio : values()) {
			if(code.equals(rio.getCode())){
				return rio;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
