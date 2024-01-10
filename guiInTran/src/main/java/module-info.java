import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.searchgui.SearchGUIInTran;

module guiInTran {
    requires core;
    provides SearchProcessor with SearchGUIInTran;
}