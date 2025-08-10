package com.unithon.aeio.domain.classes.service;

import com.unithon.aeio.domain.classes.dto.ClassRequest;
import com.unithon.aeio.domain.classes.dto.ClassResponse;

public interface ClassService {
    ClassResponse.ClassId createClass(ClassRequest.ClassInfo request);
}
