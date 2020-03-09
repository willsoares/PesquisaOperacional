package conteiner;

public class Data {
	private int qtdItens;
	private int qtdConteiners;
	private int usoMaximoItem;
	private int conteinerCapacidade;
	private int conteinerVolume;
	
	private double itensLucro[];
	private double itensPeso[];
	private double itensVolume[];

	/**
	 * @return the qtdItens
	 */
	public int getQtdItens() {
		return qtdItens;
	}

	/**
	 * @return the itensVolume
	 */
	public double[] getItensVolume() {
		return itensVolume;
	}

	/**
	 * @param itensVolume the itensVolume to set
	 */
	public void setItensVolume(double itensVolume[]) {
		this.itensVolume = itensVolume;
	}

	/**
	 * @return the itensPeso
	 */
	public double[] getItensPeso() {
		return itensPeso;
	}

	/**
	 * @param itensPeso the itensPeso to set
	 */
	public void setItensPeso(double itensPeso[]) {
		this.itensPeso = itensPeso;
	}

	/**
	 * @return the itensLucro
	 */
	public double[] getItensLucro() {
		return itensLucro;
	}

	/**
	 * @param itensLucro the itensLucro to set
	 */
	public void setItensLucro(double itensLucro[]) {
		this.itensLucro = itensLucro;
	}

	/**
	 * @return the conteinerVolume
	 */
	public int getConteinerVolume() {
		return conteinerVolume;
	}

	/**
	 * @param conteinerVolume the conteinerVolume to set
	 */
	public void setConteinerVolume(int conteinerVolume) {
		this.conteinerVolume = conteinerVolume;
	}

	/**
	 * @return the conteinerCapacidade
	 */
	public int getConteinerCapacidade() {
		return conteinerCapacidade;
	}

	/**
	 * @param conteinerCapacidade the conteinerCapacidade to set
	 */
	public void setConteinerCapacidade(int conteinerCapacidade) {
		this.conteinerCapacidade = conteinerCapacidade;
	}

	/**
	 * @return the usoMaximoItem
	 */
	public int getUsoMaximoItem() {
		return usoMaximoItem;
	}

	/**
	 * @param usoMaximoItem the usoMaximoItem to set
	 */
	public void setUsoMaximoItem(int usoMaximoItem) {
		this.usoMaximoItem = usoMaximoItem;
	}

	/**
	 * @return the qtdConteiners
	 */
	public int getQtdConteiners() {
		return qtdConteiners;
	}

	/**
	 * @param qtdConteiners the qtdConteiners to set
	 */
	public void setQtdConteiners(int qtdConteiners) {
		this.qtdConteiners = qtdConteiners;
	}

	/**
	 * @param qtdItens the qtdItens to set
	 */
	public void setQtdItens(int qtdItens) {
		this.qtdItens = qtdItens;
	}
}