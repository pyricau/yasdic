package info.piwai.yasdic.example.impl;

public class Cycle {

	private Cycle	dependency;

	public Cycle() {

	}

	public Cycle(Cycle dependency) {
		this.dependency = dependency;
	}

	public void setDependency(Cycle dependency) {
		this.dependency = dependency;
	}

}
