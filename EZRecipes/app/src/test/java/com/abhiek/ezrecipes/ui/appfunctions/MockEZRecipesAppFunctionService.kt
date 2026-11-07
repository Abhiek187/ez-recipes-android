package com.abhiek.ezrecipes.ui.appfunctions

import android.os.CancellationSignal
import androidx.appfunctions.ExecuteAppFunctionRequest
import androidx.appfunctions.ExecuteAppFunctionResponse
import androidx.appfunctions.metadata.AppFunctionMetadata
import java.util.function.Consumer

internal class MockEZRecipesAppFunctionService: BaseEZRecipesAppFunctionService() {
    override fun onExecuteFunction(
        request: ExecuteAppFunctionRequest,
        metadata: AppFunctionMetadata,
        cancellationSignal: CancellationSignal,
        callback: Consumer<ExecuteAppFunctionResponse>
    ) {
        TODO("Not yet implemented")
    }
}
