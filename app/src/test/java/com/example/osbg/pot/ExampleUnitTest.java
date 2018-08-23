package com.example.osbg.pot;

import android.util.Base64;

import com.example.osbg.pot.infrastructure.KeyGenerator;
import com.example.osbg.pot.utilities.Base58Helper;
import com.example.osbg.pot.utilities.FormatHelper;
import com.example.osbg.pot.utilities.HashCalculator;

import org.junit.Test;

import java.io.FileReader;
import java.security.spec.ECField;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Base58encode(){
        assertEquals("3yZe7d", Base58Helper.encode("test".getBytes()));
    }

    @Test
    public void Base58decode(){
        assertEquals( new byte[0], Base64.decode("YBftQaJYh8juA+OL741fA==", 0));
    }

    @Test
    public void ShouldBeUUID() {
        assertEquals(true, FormatHelper.isUUID("C95C785A-9BAE-11E8-BBAF-D78381CA54A1"));
    }
}