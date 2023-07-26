/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package policy;

public class EsaphServerPolicy
{
    public boolean hasPermission(ESP esp)
    {
        return esp.onPolicyCheck();
    }
}
