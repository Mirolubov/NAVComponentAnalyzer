import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import search.SearchTransactionInValidate;

module transactionInValidate {
    requires core;
    provides SearchProcessor with SearchTransactionInValidate;
}