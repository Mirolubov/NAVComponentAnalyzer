import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import search.SearchGUIInTran;

module guiInTran {
    requires core;
    provides SearchProcessor with SearchGUIInTran;
}