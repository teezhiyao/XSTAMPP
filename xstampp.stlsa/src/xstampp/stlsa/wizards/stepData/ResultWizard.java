package xstampp.stlsa.wizards.stepData;

import messages.Messages;
import xstampp.stlsa.Activator;
import xstampp.stlsa.messages.StlsaMessages;
import xstampp.stlsa.ui.results.ResultEditor;
import xstampp.stlsa.util.jobs.ICSVExportConstants;
import xstampp.stlsa.wizards.AbstractPrivacyExportWizard;
import xstampp.ui.wizards.CSVExportPage;

public class ResultWizard extends AbstractPrivacyExportWizard{

	public ResultWizard() {
		super(ResultEditor.ID);
		String[] filters = new String[] { "*.csv" }; //$NON-NLS-1$
		this.setExportPage(new CSVExportPage(filters, StlsaMessages.Results + Messages.AsDataSet, Activator.PLUGIN_ID));
	}

	@Override
	public boolean performFinish() {
		return this.performCSVExport(ICSVExportConstants.RESULT);
	}
}
