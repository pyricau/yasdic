package info.piwai.yasdic.example.impl;

import info.piwai.yasdic.example.interfaces.IController;
import info.piwai.yasdic.example.interfaces.IService;
import info.piwai.yasdic.example.interfaces.IView;

public class ControllerImpl implements IController {

	private IService	service;
	private IView		view;

	public String execute() {
		return view.renderView(service.getValue());
	}

	public ControllerImpl(IService service, IView view) {
		this.service = service;
		this.view = view;
	}

}
