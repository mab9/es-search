package ch.mab.search.es.business;

import ch.mab.search.es.model.SearchDoc;
import ch.mab.search.es.model.SearchQuery;
import ch.mab.search.secasignbox.model.Archivespace;
import ch.mab.search.secasignbox.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    public SearchService() {
    }

    public boolean saveDoc(String doc) {
        System.out.println(doc);
        return true;
    }

    void saveDoc(SearchDoc searchDoc, Archivespace archivespace, User user) {
    }

    void updateDoc(SearchDoc searchDoc, User user) {
    }

    void deleteDoc(Long docId, User user) {
    }

    List<SearchDoc> searchByQuery(SearchQuery searchQuery, User user) {
        return new ArrayList<>();
    }
}
