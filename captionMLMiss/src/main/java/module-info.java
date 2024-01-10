import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import search.SearchCaptionMLMiss;

module captionMLMiss {
    requires core;
    provides SearchProcessor with SearchCaptionMLMiss;
}