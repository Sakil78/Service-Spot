import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, KeyRound, CheckCircle, ArrowLeft } from 'lucide-react';

/**
 * Forgot Password Page - Multi-step password reset flow
 * Step 1: Enter email
 * Step 2: Enter 6-digit code
 * Step 3: Enter new password
 * Step 4: Success confirmation
 */
const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1: Email, 2: Code, 3: Password, 4: Success
  const [email, setEmail] = useState('');
  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [generatedToken, setGeneratedToken] = useState(''); // Store token from step 1

  /**
   * Step 1: Request reset token
   */
  const handleRequestReset = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8080/api/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });

      const data = await response.json();

      if (response.ok) {
        // Store the generated token (in production, this would be sent via email)
        setGeneratedToken(data.data.resetToken);
        setStep(2);
      } else {
        setError(data.message || 'Failed to send reset code');
      }
    } catch (err) {
      setError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Step 2: Verify reset token
   */
  const handleVerifyToken = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch(
        `http://localhost:8080/api/auth/verify-reset-token?email=${encodeURIComponent(email)}&token=${resetToken}`
      );

      const data = await response.json();

      if (response.ok && data.data === true) {
        setStep(3);
      } else {
        setError('Invalid or expired code. Please try again.');
      }
    } catch (err) {
      setError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Step 3: Reset password
   */
  const handleResetPassword = async (e) => {
    e.preventDefault();

    // Validate password match
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (newPassword.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8080/api/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email,
          resetToken,
          newPassword
        })
      });

      const data = await response.json();

      if (response.ok) {
        setStep(4);
      } else {
        setError(data.message || 'Failed to reset password');
      }
    } catch (err) {
      setError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Render appropriate step content
   */
  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <form onSubmit={handleRequestReset} className="space-y-4">
            <div className="text-center mb-6">
              <div className="feature-icon mx-auto mb-4">
                <Mail size={32} />
              </div>
              <h2 className="text-3xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-400">
                Forgot Password?
              </h2>
              <p className="text-gray-300 mt-2">
                No worries! Enter your email and we'll send you a reset code.
              </p>
            </div>

            {error && (
              <div className="bg-red-50 border-2 border-red-200 text-red-700 px-4 py-3 rounded-xl mb-4 flex items-start gap-3">
                <span className="text-red-500 font-bold text-xl">⚠️</span>
                <span className="font-medium">{error}</span>
              </div>
            )}

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-slate-300 mb-1">
                Email Address
              </label>
              <div className="relative">
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="input-field pl-12"
                  placeholder="you@email.com"
                />
                <div className="absolute left-3 top-3 text-slate-400">
                  <Mail size={16} />
                </div>
              </div>
            </div>

            <button type="submit" disabled={loading} className="w-full btn-primary">
              {loading ? 'Sending Code...' : 'Send Reset Code'}
            </button>

            <div className="text-center">
              <Link to="/login" className="text-blue-400 hover:text-blue-300 flex items-center justify-center gap-2">
                <ArrowLeft size={16} />
                Back to Login
              </Link>
            </div>
          </form>
        );

      case 2:
        return (
          <form onSubmit={handleVerifyToken} className="space-y-4">
            <div className="text-center mb-6">
              <div className="feature-icon mx-auto mb-4 from-green-400 to-cyan-400">
                <KeyRound size={32} />
              </div>
              <h2 className="text-3xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-400">
                Enter Reset Code
              </h2>
              <p className="text-gray-300 mt-2">
                We've sent a 6-digit code to <strong>{email}</strong>
              </p>
              <div className="mt-4 p-4 bg-blue-50 border-2 border-blue-200 rounded-xl">
                <p className="text-blue-700 font-semibold text-sm">
                  📧 Check your email inbox
                </p>
                <p className="text-blue-900 text-sm mt-2">
                  A 6-digit verification code has been sent to your email address.
                  Please check your inbox (and spam folder) for the code.
                </p>
                <p className="text-blue-600 text-xs mt-2">
                  The code will expire in 15 minutes.
                </p>
              </div>
              {/* DEVELOPMENT ONLY: Display token on screen (remove in production) */}
              {process.env.NODE_ENV === 'development' && generatedToken && (
                <div className="mt-4 p-4 bg-yellow-50 border-2 border-yellow-200 rounded-xl">
                  <p className="text-yellow-700 font-semibold text-sm">
                    ⚠️ Development Mode - Token visible for testing
                  </p>
                  <p className="text-yellow-900 font-bold text-2xl tracking-widest mt-2">
                    {generatedToken}
                  </p>
                  <p className="text-yellow-600 text-xs mt-2">
                    This will NOT be shown in production builds
                  </p>
                </div>
              )}
            </div>

            {error && (
              <div className="bg-red-50 border-2 border-red-200 text-red-700 px-4 py-3 rounded-xl mb-4 flex items-start gap-3">
                <span className="text-red-500 font-bold text-xl">⚠️</span>
                <span className="font-medium">{error}</span>
              </div>
            )}

            <div>
              <label htmlFor="resetToken" className="block text-sm font-medium text-slate-300 mb-1">
                6-Digit Code
              </label>
              <div className="relative">
                <input
                  type="text"
                  id="resetToken"
                  value={resetToken}
                  onChange={(e) => setResetToken(e.target.value.replace(/\D/g, '').slice(0, 6))}
                  required
                  maxLength={6}
                  pattern="[0-9]{6}"
                  className="input-field pl-12 text-center text-2xl tracking-widest font-bold"
                  placeholder="000000"
                />
                <div className="absolute left-3 top-3 text-slate-400">
                  <KeyRound size={16} />
                </div>
              </div>
              <p className="text-xs text-gray-400 mt-2">
                Code expires in 15 minutes
              </p>
            </div>

            <button type="submit" disabled={loading || resetToken.length !== 6} className="w-full btn-primary">
              {loading ? 'Verifying...' : 'Verify Code'}
            </button>

            <div className="text-center">
              <button
                type="button"
                onClick={() => setStep(1)}
                className="text-blue-400 hover:text-blue-300"
              >
                Resend Code
              </button>
            </div>
          </form>
        );

      case 3:
        return (
          <form onSubmit={handleResetPassword} className="space-y-4">
            <div className="text-center mb-6">
              <div className="feature-icon mx-auto mb-4 from-purple-400 to-pink-400">
                <Lock size={32} />
              </div>
              <h2 className="text-3xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-400">
                Create New Password
              </h2>
              <p className="text-gray-300 mt-2">
                Choose a strong password for your account
              </p>
            </div>

            {error && (
              <div className="bg-red-50 border-2 border-red-200 text-red-700 px-4 py-3 rounded-xl mb-4 flex items-start gap-3">
                <span className="text-red-500 font-bold text-xl">⚠️</span>
                <span className="font-medium">{error}</span>
              </div>
            )}

            <div>
              <label htmlFor="newPassword" className="block text-sm font-medium text-slate-300 mb-1">
                New Password
              </label>
              <div className="relative">
                <input
                  type="password"
                  id="newPassword"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                  minLength={6}
                  className="input-field pl-12"
                  placeholder="••••••••"
                />
                <div className="absolute left-3 top-3 text-slate-400">
                  <Lock size={16} />
                </div>
              </div>
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-slate-300 mb-1">
                Confirm Password
              </label>
              <div className="relative">
                <input
                  type="password"
                  id="confirmPassword"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  minLength={6}
                  className="input-field pl-12"
                  placeholder="••••••••"
                />
                <div className="absolute left-3 top-3 text-slate-400">
                  <Lock size={16} />
                </div>
              </div>
            </div>

            <button type="submit" disabled={loading} className="w-full btn-primary">
              {loading ? 'Resetting Password...' : 'Reset Password'}
            </button>
          </form>
        );

      case 4:
        return (
          <div className="text-center space-y-6">
            <div className="feature-icon mx-auto from-green-400 to-emerald-400">
              <CheckCircle size={48} />
            </div>
            <h2 className="text-3xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-green-400 to-emerald-400">
              Password Reset Successfully!
            </h2>
            <p className="text-gray-300">
              Your password has been changed. You can now login with your new password.
            </p>
            <button
              onClick={() => navigate('/login')}
              className="w-full btn-primary"
            >
              Go to Login
            </button>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] py-12 px-4 bg-gray-50">
      <div className="max-w-md mx-auto">
        <div className="bg-gradient-to-br from-slate-800 via-gray-800 to-slate-900 shadow-xl border border-gray-700/50 p-8 rounded-3xl">
          {/* Progress Indicator */}
          {step < 4 && (
            <div className="flex items-center justify-center gap-2 mb-8">
              {[1, 2, 3].map((s) => (
                <div
                  key={s}
                  className={`h-2 flex-1 rounded-full transition-all duration-300 ${
                    s <= step
                      ? 'bg-gradient-to-r from-blue-500 to-purple-500'
                      : 'bg-gray-600'
                  }`}
                />
              ))}
            </div>
          )}

          {renderStepContent()}
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;

