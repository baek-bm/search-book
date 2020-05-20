package com.baek.app.searchbook.service;

import com.baek.app.searchbook.dto.Member;
import com.baek.app.searchbook.repository.MemberRepository;
import com.baek.app.searchbook.repository.Role;
import com.baek.app.searchbook.repository.entity.MemberEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public String joinUser(Member member) throws Exception {
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new Exception("Already Joined");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        MemberEntity e = new MemberEntity();
        BeanUtils.copyProperties(member, e);
        String email = memberRepository.save(e).getEmail();
        return email;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<MemberEntity> userEntityWrapper = memberRepository.findByEmail(userEmail);
        if (userEntityWrapper.isPresent()) {
            MemberEntity userEntity = userEntityWrapper.get();

            List<GrantedAuthority> authorities = new ArrayList<>();
            if (userEntity.getIsRoleAdmin()) authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
            if (userEntity.getIsRoleMember()) authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
            if (userEntity.getIsRoleMonitor()) authorities.add(new SimpleGrantedAuthority(Role.MONITOR.getValue()));

            return new UserCustom(userEntity, authorities);
        }

        throw new UsernameNotFoundException("email is not exist.");
    }

    @Data
    public static class UserCustom extends User {
        private final MemberEntity memberEntity;
        private final String email;

        public UserCustom(MemberEntity memberEntity, Collection<? extends GrantedAuthority> authorities) {
            super(memberEntity.getUserName(), memberEntity.getPassword(), true, true, true, true, authorities);
            this.memberEntity = memberEntity;
            this.email = memberEntity.getEmail();
        }
    }
}
