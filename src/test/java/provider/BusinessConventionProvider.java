/*
 * Copyright (c) 2018 Thermo Fisher Scientific
 * All rights reserved.
 */


package provider;

/**
 * Provides the pointers for Business Convention Provider {@link PactProvider}
 */
public class BusinessConventionProvider extends PactProvider
{
    @Override
    protected String getProviderName()
    {
        return KnownProviders.QA_CHALLENGE_CONVENTION;
    }
}

