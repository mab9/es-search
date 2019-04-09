package ch.mab.search.business;

import org.springframework.stereotype.Service;

@Service
public class SearchService {

    public SearchService() {
    }

    public boolean saveDoc(String doc) {
        System.out.println(doc);
        return true;
    }
}
