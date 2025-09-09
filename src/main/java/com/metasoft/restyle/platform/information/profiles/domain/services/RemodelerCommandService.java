package com.metasoft.restyle.platform.information.profiles.domain.services;


import com.metasoft.restyle.platform.information.profiles.domain.model.commands.CreateRemodelerCommand;
import org.springframework.stereotype.Service;


public interface RemodelerCommandService {
    Long handle(CreateRemodelerCommand command);
}
