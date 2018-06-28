package com.dc18TokenExchange.STSserver.jwt.algorithms;

import com.dc18TokenExchange.STSserver.jwt.exceptions.SignatureVerificationException;
import com.dc18TokenExchange.STSserver.jwt.interfaces.DecodedJWT;
import com.dc18TokenExchange.STSserver.jwt.exceptions.SignatureGenerationException;
import org.apache.commons.codec.binary.Base64;

class NoneAlgorithm extends Algorithm {

    NoneAlgorithm() {
        super("none", "none");
    }

    @Override
    public void verify(DecodedJWT jwt) throws SignatureVerificationException {
        byte[] signatureBytes = Base64.decodeBase64(jwt.getSignature());
        if (signatureBytes.length > 0) {
            throw new SignatureVerificationException(this);
        }
    }

    @Override
    public byte[] sign(byte[] contentBytes) throws SignatureGenerationException {
        return new byte[0];
    }
}
