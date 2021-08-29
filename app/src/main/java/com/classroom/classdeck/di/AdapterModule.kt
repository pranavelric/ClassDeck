package com.classroom.classdeck.di

import com.classroom.classdeck.adapters.*
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

    @Provides
    fun providesEventAdapter(): EventAdapter {
        return EventAdapter()
    }

    @Provides
    fun providesQuizAdapter(): QuizAdapter {
        return QuizAdapter()
    }

    @Provides
    fun providesQaAdapter(): QuestionAdapter{
        return QuestionAdapter()
    }

}