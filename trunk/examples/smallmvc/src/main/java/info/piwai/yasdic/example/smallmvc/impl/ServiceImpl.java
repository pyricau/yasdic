package info.piwai.yasdic.example.smallmvc.impl;

import info.piwai.yasdic.example.smallmvc.interfaces.IService;

import java.util.List;

public class ServiceImpl implements IService {

	private List<String>	niceWords;

	public ServiceImpl(List<String> niceWords) {
		this.niceWords = niceWords;
	}

	public String getValue() {
		return niceWords.get((int) Math.round((Math.random() * (niceWords
				.size() - 1))));
	}

}
