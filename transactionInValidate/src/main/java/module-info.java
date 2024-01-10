import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.searchvalidate.SearchTransactionInValidate;

module transactionInValidate {
    requires core;
    provides SearchProcessor with SearchTransactionInValidate;
}