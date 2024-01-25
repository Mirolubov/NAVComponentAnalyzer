import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.siftusage.SearchSiftUsage;

module siftUsage {
    requires core;
    provides SearchProcessor with SearchSiftUsage;

}