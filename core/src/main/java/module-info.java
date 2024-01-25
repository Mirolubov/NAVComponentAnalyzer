import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;

module core {
    requires java.desktop;
    exports com.company.navcomponentanalyzer.core.model.search;
    exports com.company.navcomponentanalyzer.core.model;
    exports com.company.navcomponentanalyzer.core.config;
    exports com.company.navcomponentanalyzer.core.model.parser;
    exports com.company.navcomponentanalyzer.core.model.object;
    exports com.company.navcomponentanalyzer.core.model.object.element;
    uses SearchProcessor;
}