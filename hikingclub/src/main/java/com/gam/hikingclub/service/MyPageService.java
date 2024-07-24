package com.gam.hikingclub.service;

import com.gam.hikingclub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {

    @Autowired
    private MemberRepository memberRepository;

    
}
