package xstampp.stlsa.wizards.pdf;

import messages.Messages;
import xstampp.stlsa.Activator;
import xstampp.stlsa.messages.SecMessages;
import xstampp.stlsa.ui.results.ResultEditor;
import xstampp.stlsa.wizards.AbstractPrivacyExportWizard;
import xstampp.ui.wizards.TableExportPage;

public class ResultPDFWizard extends AbstractPrivacyExportWizard{
	public ResultPDFWizard() {
		super(ResultEditor.ID);
		String[] filters = new String[] { "*.pdf" }; //$NON-NLS-1$ 
		this.setExportPage(new TableExportPage(filters,
				SecMessages.Results + Messages.AsPDF, Activator.PLUGIN_ID));
		
	}

	@Override
	public boolean performFinish() {
		return this.performXSLExport(				
				"/fopResults.xsl", false, SecMessages.Results, false); ////$NON-NLS-1$
	}
}
