package com.gutotech.loteriasapi.consumer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gutotech.loteriasapi.model.Estado;
import com.gutotech.loteriasapi.model.Premiacao;
import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.model.ResultadoId;
import com.gutotech.loteriasapi.util.SSLHelper;

@Component
public class Consumer {

	private final String BASE_URL = "https://www.megaloterias.com.br/";

	public Resultado getResultado(String loteria, int concurso) throws IOException {
		return getResultado(loteria, String.valueOf(concurso));
	}

	public Resultado getResultado(String loteria, String concurso) throws IOException {
		if (concurso == null) {
			concurso = "";
		}

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Document doc = SSLHelper.getConnection(BASE_URL + loteria + "/resultados" + (!concurso.isEmpty() ? "?drawNumber=" + concurso : "")).get();
		Element resultElement = doc.getElementsByClass("lottery-totem").first();

		Resultado resultado = new Resultado(
				new ResultadoId(loteria, concurso.equals("") ? 0 : Integer.parseInt(concurso)));

		// Nome da Loteria
		resultado.setNome(resultElement.select(".result__title").text());

		// Concurso
		resultado.setConcurso(
				Integer.valueOf(resultElement.select(".result__draw > strong").text()));

		// Data
		String data = resultElement.select(".result__draw-date > strong").text();
		if (data.toUpperCase().equals("HOJE")) {
			data = dateFormat.format(new Date());
		}
		resultado.setData(data);

		// Local
		resultado.setLocal(resultElement.select(".result__local strong").text());

		// Dezenas
		Elements numbers = resultElement.select(".lot-bg-light > span");
		
		if (numbers.size() == 0) {
			numbers = resultElement.select(".result__federal-item__val");
		}
		
		for (Element element : numbers) {
			try {
				Integer.parseInt(element.text());
			} catch (Exception e) {
				continue;
			}
			resultado.getDezenas().add(element.text());
		}

		// Premiacoes
		Elements premiacoesTrs = resultElement.select(".result__table-prize tr");
		
		int count = 0;
		for (Element tr : premiacoesTrs) {
			if (count++ == 0) {
				continue;
			}

			Premiacao premiacao = new Premiacao();

			Elements tds = tr.select("td");
			
			premiacao.setAcertos(tds.get(0).text());

			try {
				premiacao.setVencedores(
						Integer.parseInt(tds.get(1).text()));
			} catch (Exception e) {
				premiacao.setVencedores(0);
			}

			premiacao.setPremio(tds.get(2).text());

			resultado.getPremiacoes().add(premiacao);
		}

		// Estados premiados
		Element buttonWin = resultElement.getElementsByClass("button-win").first();
		if (buttonWin != null) {
			String json = buttonWin.attr("data-estados-premiados");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<Estado> estados = objectMapper.readValue(json, new TypeReference<List<Estado>>() {});
			resultado.setEstadosPremiados(estados);
		}

		// Acumulado
		resultado.setAcumulou(resultElement.select(".result__content__wrap p strong").size() > 0);
		
		Element valorAcumuladoElement = resultElement.select(".result__acumulations div div.lot-color").first();
		
		if (valorAcumuladoElement != null) {
			resultado.setAcumuladaProxConcurso(valorAcumuladoElement.text());
		}

		
		Element nextDrawElement = doc.getElementsByClass("banner-nextdraw__info").first();
		// Data Proximo Concursoresult__federal-item__val
		String dataProxConcurso = nextDrawElement.select(".banner-nextdraw__draw-date strong").text();

		if (dataProxConcurso != null) {
			if (dataProxConcurso.toLowerCase().equals("hoje")) {
				dataProxConcurso = dateFormat.format(new Date());
			} else if (dataProxConcurso.toLowerCase().equals("amanhã")) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, 1);
				dataProxConcurso = dateFormat.format(calendar.getTime());
			}
		}
		resultado.setDataProxConcurso(dataProxConcurso);

		// Time de coracao
		if (resultElement.select(".tens-grid-timemania-second-result .fa-heart").size() > 0) {
			resultado.setTimeCoracao(resultElement.select(".tens-grid-timemania-second-result span").text());
		}

		// Mes de sorte
		if (resultElement.select(".tens-grid-timemania-second-result .fa-calendar").size() > 0) {
			resultado.setMesSorte(resultElement.select(".tens-grid-timemania-second-result span").text());
		}

		return resultado;
	}

}
