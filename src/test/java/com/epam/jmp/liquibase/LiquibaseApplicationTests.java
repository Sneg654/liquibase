package com.epam.jmp.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;


@DataJpaTest
class LiquibaseApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    void userDataShouldBeAddedToUserTable() {
        List<User> users = jdbcTemplate
            .query("SELECT id, username, first_name, last_name FROM user", (rs, rowNum) -> new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
            ));

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getUserName(), is("olgaspresova"));
        assertThat(users.get(0).getFirstName(), is("Olga"));
        assertThat(users.get(0).getLastName(), is("Spresova"));
    }

    @Test
    void companyDataShouldBeAddedToCompanyTable() {
        List<Company> companies = getCompanies();

        assertThat(companies.size(), is(1));
    }

    private List<Company> getCompanies() {
        return jdbcTemplate
                .query("SELECT id, name FROM company", (rs, rowNum) -> new Company(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
    }

    @Test
    void checkThatUserAddedToCompany() {
        List<Company> companies = getCompanies();

        assertThat(companies.size(), is(1));

        List<User> users = jdbcTemplate
                .query("SELECT tu.* " +
                                "FROM company_user cu " +
                                "JOIN user tu ON tu.id = cu.user_id " +
                                "WHERE cu.company_id = " + companies.get(0).getId(),
                        (rs, rowNum) -> new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("first_name"),
                                rs.getString("last_name")
                        ));

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getUserName(), is("olgaspresova"));
    }

}
