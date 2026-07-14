package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void save() throws SQLException {
        Member member = new Member("memberV0", 10000);
        repository.save(member);
    }

    @Test
    void findById() throws SQLException {

        Member member = new Member("memberV2", 100);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);

        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void update() throws SQLException {
        Member member = new Member("memberV5", 100);
        repository.save(member);

        repository.update(member.getMemberId(),20);
        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember.getMoney()).isEqualTo(20);
    }

    @Test
    void delete() throws SQLException {
        Member member = new Member("memberV7", 100);
        repository.save(member);

        repository.delete(member.getMemberId());
        assertThatThrownBy(()-> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}