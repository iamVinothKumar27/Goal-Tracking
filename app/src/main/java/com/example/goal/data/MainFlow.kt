package com.example.goal.data

sealed  class MainFlow {
    data class toastSuccess(var msg :String): MainFlow()

}