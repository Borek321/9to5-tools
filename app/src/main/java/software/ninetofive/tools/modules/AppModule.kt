package software.ninetofive.tools.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import software.ninetofive.tools.TestActivity

@Module
abstract class AppModule {

    @ContributesAndroidInjector()
    abstract fun testActivity(): TestActivity

}