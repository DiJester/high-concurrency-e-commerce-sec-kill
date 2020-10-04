package com.liudi.happyshopping.exception;

import com.liudi.happyshopping.result.CodeMsg;

public class GlobalException extends RuntimeException{

    private CodeMsg cm;

    public CodeMsg getCm() {
        return cm;
    }

    public GlobalException(CodeMsg cm){
        super();
        this.cm = cm;

    }

}