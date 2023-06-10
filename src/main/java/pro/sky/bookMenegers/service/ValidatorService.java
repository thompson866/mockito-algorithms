package pro.sky.bookMenegers.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.sky.bookMenegers.exception.IncorrectNameException;
import pro.sky.bookMenegers.exception.IncorrectSurnameException;

@Service
public class ValidatorService {

    public String validateName(String name) {
        String[] names = name.split("-");
        for (int i = 0; i < names.length; i++) {
            if (!StringUtils.isAlpha(names[i])) {
                throw new IncorrectSurnameException();
            }
            names[i] = StringUtils.capitalize(names[i].toLowerCase());
        }
        return String.join("-", names);
    }

    public String validateSurname(String surname) {
        String[] surnames = surname.split("-");
        for (int i = 0; i < surnames.length; i++) {
            if (!StringUtils.isAlpha(surnames[i])) {
                throw new IncorrectSurnameException();
            }
            surnames[i] = StringUtils.capitalize(surnames[i].toLowerCase());
        }
        return String.join("-", surnames);
    }

}
