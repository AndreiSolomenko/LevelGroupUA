package com.levelgroup.services;

import com.levelgroup.model.RiaLGObject;
import com.levelgroup.repo.RiaLGObjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

@Service
public class RiaLGObjectService {

    private final RiaLGObjectRepository riaLGObjectRepository;

    public RiaLGObjectService(RiaLGObjectRepository riaLGObjectRepository) {
        this.riaLGObjectRepository = riaLGObjectRepository;
    }


    @Transactional
    public void add(RiaLGObject riaLGObject) {
        riaLGObjectRepository.save(riaLGObject);
    }

    @Transactional
    public RiaLGObject getById(String localRealtyId) {
        return riaLGObjectRepository.findRiaLGObjectByLocalRealtyId(localRealtyId);
    }

        @Transactional(readOnly = true)
    public ArrayList<RiaLGObject>get() {

        var objects = riaLGObjectRepository.findAll();
        var result = new ArrayList<RiaLGObject>();

        objects.forEach(object -> result.add(object));
        return result;
    }

}
