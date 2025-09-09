package com.metasoft.restyle.platform.information.profiles.application.internal.queryservices;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetAllContractorQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetContractorByIdQuery;
import com.metasoft.restyle.platform.information.profiles.domain.services.ContractorQueryService;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.ContractorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractorQueryServiceImpl implements ContractorQueryService {

    private final ContractorRepository contractorRepository;

    public ContractorQueryServiceImpl(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    /*@Override
    public boolean existsByPhoneNumber(String phone) {
        //Verificar si el número de telefono existe en contractors
        return contractorRepository.existsByPhone(phone);
    }*/

    @Override
    public List<Contractor> handle(GetAllContractorQuery query) {
        //Obtener todos los contractors
        return contractorRepository.findAll();
    }

    @Override
    public Optional<Contractor> handle(GetContractorByIdQuery query) {
        //Obtener un contractor por id
        return contractorRepository.findById(query.getId());
    }
}
