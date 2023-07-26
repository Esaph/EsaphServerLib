/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package esaphsession;

import java.math.BigInteger;
import java.security.SecureRandom;

public class EsaphServerSessionManager
{
    public String generateSessionToken()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
