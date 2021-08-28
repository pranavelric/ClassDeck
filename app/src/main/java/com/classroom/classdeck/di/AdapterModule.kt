package com.classroom.classdeck.di

import com.classroom.classdeck.adapters.AnnouncementAdapter
import com.classroom.classdeck.adapters.AssignmentAdapter
import com.classroom.classdeck.adapters.CourseAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@InstallIn(ActivityComponent::class)
@Module
class AdapterModule {

    @Provides
    fun providesCourseAdapter(): CourseAdapter {
        return CourseAdapter()
    }

    @Provides
    fun providesAnnouncementAdapter(): AnnouncementAdapter {
        return AnnouncementAdapter()
    }

    @Provides
    fun providesAssignmentAdapter(): AssignmentAdapter {
        return AssignmentAdapter()
    }

}