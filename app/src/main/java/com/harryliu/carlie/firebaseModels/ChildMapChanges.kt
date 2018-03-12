package com.harryliu.carlie.firebaseModels

/**
 * @author Harry Liu
 * @version Mar 2, 2018
 */

class ChildMapChanges<T: FireBaseModel>(val addModels: List<Pair<String, Pair<T, RealTimeValue<T>>>>,
        val updatedModels: List<Pair<String, Pair<T, RealTimeValue<T>>>>,
        val deletedModels: List<Pair<String, Pair<T, RealTimeValue<T>>>>) {
    override fun toString(): String {
        return """
            ChildMapChanges
                addModels=$addModels
                updatedModels=$updatedModels
                deletedModels=$deletedModels
            """
    }
}