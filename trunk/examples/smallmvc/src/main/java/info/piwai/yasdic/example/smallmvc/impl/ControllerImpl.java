package info.piwai.yasdic.example.smallmvc.impl;

import info.piwai.yasdic.example.smallmvc.interfaces.IController;
import info.piwai.yasdic.example.smallmvc.interfaces.IService;
import info.piwai.yasdic.example.smallmvc.interfaces.IView;

/**
 * 
 * @author pricau
 * 
 */
public class ControllerImpl implements IController {

	private IService	service;
	private IView		view;

	public ControllerImpl(IService service, IView view) {
		this.service = service;
		this.view = view;
	}

	public void execute() {
		view.renderView(service.getValue());
	}

}
