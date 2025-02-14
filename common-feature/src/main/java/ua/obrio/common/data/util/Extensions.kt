package ua.obrio.common.data.util

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import ua.obrio.common.presentation.util.Constants.Storage.DATA_STORE_NAME

val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)