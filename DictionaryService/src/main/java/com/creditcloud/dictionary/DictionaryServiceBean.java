/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.dictionary;

import com.creditcloud.dictionary.api.DictionaryService;
import com.creditcloud.model.util.Regions;
import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Remote
@Stateless
public class DictionaryServiceBean implements DictionaryService {

    @Override
    public List<Regions.Entry> getRegionList(String code) {
        return Regions.getRegionList(code);
    }
}
