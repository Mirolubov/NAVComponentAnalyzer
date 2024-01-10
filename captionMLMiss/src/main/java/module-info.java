import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.searchml.SearchCaptionMLMiss;

module captionMLMiss {
    requires core;
    provides SearchProcessor with SearchCaptionMLMiss;
}